package com.flashcards.tyky.service;

import com.flashcards.tyky.domain.Card;
import com.flashcards.tyky.exception.BadRequestException;
import com.flashcards.tyky.exception.NotFoundException;
import com.flashcards.tyky.repository.CardRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Getter
@Setter
public class CardService {
    private static final Double PERFECT_RECALL_BONUS = 0.1;
    private static final Integer MAX_QUALITY_SCORE = 5;
    private static final Double RECALL_PENALTY_BASE = 0.08;
    private static final Double RECALL_PENALTY_MULTIPLIER = 0.02;

    @Autowired
    private final CardRepository repository;

    public List<Card> listAll(){
        return repository.findAll();
    }

    public Card listById(UUID id){
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Card")
        );
    }

    public Card create(Card card){
        return repository.save(card);
    }

    public Card update(UUID id, Card card){
        Card exists = listById(id);
        exists.setFront(card.getFront());
        exists.setBack(card.getBack());
        exists.setUpdatedAt(LocalDateTime.now());

        return create(exists);
    }

    public void delete(UUID id){
        Card exists = listById(id);
        repository.delete(exists);
    }

    public void review(UUID id, Integer qualityScore){
        Card exists = listById(id);
        LocalDateTime now = LocalDateTime.now();

        if(now.isBefore(exists.getReviewAt())){
            throw new BadRequestException(
                "A card cannot be reviewed before the minimum date specified by the `reviewAt` attribute."
            );
        }

        Double newEasinessFactor = calculateEasinessFactor(
                exists.getEasinessFactor(),
                qualityScore
        );

        switch(qualityScore){
            case 0, 1, 2 -> {
                exists.setRepetitionCount(0);
                exists.setReviewInterval(1);
                exists.setReviewAt(
                    now.plusMinutes(1)
                );
            }
            default -> {
                exists.setRepetitionCount(exists.getRepetitionCount() + 1);

                switch(exists.getRepetitionCount()){
                    case 1 -> {
                        exists.setReviewInterval(1);
                        exists.setReviewAt(now.plusMinutes(1));
                    }
                    case 2 -> {
                        exists.setReviewInterval(2);
                        exists.setReviewAt(now.plusDays(1));
                    }
                    default -> {
                        Double newIntervalDouble = exists.getReviewInterval() * newEasinessFactor;
                        Integer newInterval = (int) Math.round(newIntervalDouble);

                        exists.setReviewInterval(newInterval);
                        exists.setReviewAt(
                            now.plusDays(newInterval)
                        );
                    }
                }
            }
        }

        exists.setEasinessFactor(newEasinessFactor);
        create(exists);
    }

    private Double calculateEasinessFactor(Double currentEasinessFactor, Integer qualityScore){
        Double easinessFactor = currentEasinessFactor +
                (PERFECT_RECALL_BONUS - (MAX_QUALITY_SCORE - qualityScore) *
                (RECALL_PENALTY_BASE + (MAX_QUALITY_SCORE - qualityScore) *
                RECALL_PENALTY_MULTIPLIER));

        if(easinessFactor < 1.3) easinessFactor = 1.3;
        return easinessFactor;
    }
}
