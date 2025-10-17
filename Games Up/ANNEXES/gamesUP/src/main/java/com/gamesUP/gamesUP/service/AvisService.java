package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.entity.Avis;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.AvisRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvisService  {

    private final AvisRepository avisRepository;
    private final GameRepository gameRepository;


    public AvisDTO create(AvisDTO avisDTO) {
        Game game = gameRepository.findById(avisDTO.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found: " + avisDTO.getGameId()));
        Avis avis = toEntity(avisDTO);
        avis.setGame(game);
        Avis saved = avisRepository.save(avis);
        return toDto(saved);
    }


    public AvisDTO update(Long id, AvisDTO avisDTO) {
        Avis existing = avisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avis not found: " + id));
        existing.setCommentaire(avisDTO.getCommentaire());
        existing.setNote(avisDTO.getNote());
        if (avisDTO.getGameId() != null && !avisDTO.getGameId().equals(existing.getGame() != null ? existing.getGame().getId() : null)) {
            Game game = gameRepository.findById(avisDTO.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found: " + avisDTO.getGameId()));
            existing.setGame(game);
        }
        Avis saved = avisRepository.save(existing);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!avisRepository.existsById(id)) {
            throw new RuntimeException("Avis not found: " + id);
        }
        avisRepository.deleteById(id);
    }

    public AvisDTO findById(Long id) {
        Avis avis = avisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avis not found: " + id));
        return toDto(avis);
    }

    public List<AvisDTO> findAll() {
        return avisRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Avis toEntity(AvisDTO dto) {
        Avis a = new Avis();
        a.setId(dto.getId());
        a.setCommentaire(dto.getCommentaire());
        a.setNote(dto.getNote());
        return a;
    }

    private AvisDTO toDto(Avis a) {
        AvisDTO dto = AvisDTO.builder()
                .id(a.getId())
                .commentaire(a.getCommentaire())
                .note(a.getNote())
                .gameId(a.getGame() != null ? a.getGame().getId() : null)
                .build();
        return dto;
    }
}