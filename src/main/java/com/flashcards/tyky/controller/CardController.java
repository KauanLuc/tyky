package com.flashcards.tyky.controller;

import com.flashcards.tyky.domain.Card;
import com.flashcards.tyky.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    @Autowired
    private final CardService service;

    @GetMapping
    public ResponseEntity<List<Card>> listAll(){
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> listById(@PathVariable UUID id){
        return ResponseEntity.ok(service.listById(id));
    }

    @PostMapping
    public ResponseEntity<Card> create(@RequestBody Card card){
        return ResponseEntity.created(null).body(service.create(card));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Card> update(@PathVariable UUID id, @RequestBody Card card){
        return ResponseEntity.ok(service.update(id, card));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/review/{id}/{qualityScore}")
    public ResponseEntity<Void> review(
            @PathVariable
            UUID id,

            @PathVariable
            @Valid
            @Min(0)
            @Max(5)
            Integer qualityScore
    ){
        service.review(id, qualityScore);
        return ResponseEntity.noContent().build();
    }
}
