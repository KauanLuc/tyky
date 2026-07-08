package com.flashcards.tyky.service;

import com.flashcards.tyky.domain.Card;
import com.flashcards.tyky.exception.NotFoundException;
import com.flashcards.tyky.repository.CardRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Getter
@Setter
public class CardService {
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

        return create(exists);
    }

    public void delete(UUID id){
        Card exists = listById(id);
        repository.delete(exists);
    }
}
