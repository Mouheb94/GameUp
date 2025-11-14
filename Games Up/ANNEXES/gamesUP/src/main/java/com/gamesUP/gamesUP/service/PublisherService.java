package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PublisherDTO;
import com.gamesUP.gamesUP.entity.Publisher;
import com.gamesUP.gamesUP.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherDTO create(PublisherDTO dto) {
        publisherRepository.findByName(dto.getName()).ifPresent(p ->
        { throw new RuntimeException("Publisher already exists: " + dto.getName()); });
        Publisher saved = publisherRepository.save(toEntity(dto));
        return toDto(saved);
    }

    public PublisherDTO update(Long id, PublisherDTO dto) {
        Publisher existing = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found: " + id));
        if (!existing.getName().equals(dto.getName())) {
            publisherRepository.findByName(dto.getName()).ifPresent(p ->
            { throw new RuntimeException("Publisher already exists: " + dto.getName()); });
        }
        existing.setName(dto.getName());
        Publisher saved = publisherRepository.save(existing);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new RuntimeException("Publisher not found: " + id);
        }
        publisherRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PublisherDTO findById(Long id) {
        Publisher p = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found: " + id));
        return toDto(p);
    }

    @Transactional(readOnly = true)
    public List<PublisherDTO> findAll() {
        return publisherRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Publisher toEntity(PublisherDTO dto) {
        return Publisher.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    private PublisherDTO toDto(Publisher p) {
        return PublisherDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .build();
    }
}
