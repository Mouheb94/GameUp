package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorDTO create(AuthorDTO authorDTO) {
        Author author = toEntity(authorDTO);
        Author saved = authorRepository.save(author);
        return toDto(saved);
    }

    public AuthorDTO update(Long id, AuthorDTO authorDTO) {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
        existing.setName(authorDTO.getName());
        Author saved = authorRepository.save(existing);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
    }

    public AuthorDTO findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
        return toDto(author);
    }

    public List<AuthorDTO> findAll() {
        return authorRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Author toEntity(AuthorDTO dto) {
        Author a = new Author();
        a.setId(dto.getId());
        a.setName(dto.getName());
        return a;
    }

    private AuthorDTO toDto(Author a) {
        return AuthorDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .build();
    }
}
