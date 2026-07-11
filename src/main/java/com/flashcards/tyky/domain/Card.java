package com.flashcards.tyky.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String front;

    @NotBlank
    private String back;

    @NotNull
    @PositiveOrZero
    private Integer repetitionCount;

    @NotNull
    private Integer reviewInterval;

    @NotNull
    @DecimalMin(value = "1.3", inclusive = true)
    private Double easinessFactor;

    @NotNull
    @FutureOrPresent
    private LocalDateTime reviewAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        reviewAt = LocalDateTime.now().plusMinutes(1);
        repetitionCount = 0;
        reviewInterval = 1;
        easinessFactor = 2.5;
    }
}
