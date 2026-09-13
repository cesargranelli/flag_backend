package br.com.flagplatform.person.service;

import br.com.flagplatform.common.enums.AthleteStatus;
import br.com.flagplatform.common.enums.DocumentType;
import br.com.flagplatform.common.exception.DuplicateDocumentException;
import br.com.flagplatform.common.exception.InvalidDocumentException;
import br.com.flagplatform.common.pagination.PagedResponse;
import br.com.flagplatform.common.validation.DocumentValidator;
import br.com.flagplatform.person.PersonInfo;
import br.com.flagplatform.person.PersonLookup;
import br.com.flagplatform.person.dto.request.CreatePersonBatchItem;
import br.com.flagplatform.person.dto.request.CreatePersonBatchRequest;
import br.com.flagplatform.person.dto.request.CreatePersonRequest;
import br.com.flagplatform.person.dto.request.UpdatePersonRequest;
import br.com.flagplatform.person.dto.response.PersonBatchLineResult;
import br.com.flagplatform.person.dto.response.PersonBatchResponse;
import br.com.flagplatform.person.dto.response.PersonResponse;
import br.com.flagplatform.person.entity.PersonEntity;
import br.com.flagplatform.person.exception.PersonNotFoundException;
import br.com.flagplatform.person.mapper.PersonMapper;
import br.com.flagplatform.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PersonService implements PersonLookup {

    private final PersonMapper mapper;
    private final PersonRepository repository;

    @Transactional
    public PersonResponse create(CreatePersonRequest request) {
        validateCpf(request.cpf(), null);
        PersonEntity entity = mapper.toEntity(request);
        entity.setStatus(AthleteStatus.ACTIVE);
        return mapper.toResponse(repository.save(entity));
    }

    /**
     * Valida uma carga em lote sem gravar (dry-run). Retorna o resultado por
     * linha: {@code VALID}, {@code DUPLICATE} (CPF já existe) ou {@code INVALID}
     * (nome em branco / CPF inválido). Linhas válidas são retornadas para pré-visualização.
     */
    public PersonBatchResponse validateBatch(CreatePersonBatchRequest request) {
        List<PersonBatchLineResult> lines = new ArrayList<>();
        int valid = 0;
        for (int i = 0; i < request.persons().size(); i++) {
            CreatePersonBatchItem item = request.persons().get(i);
            int line = i + 2; // linha 1 = cabeçalho
            if (item.name() == null || item.name().isBlank()) {
                lines.add(new PersonBatchLineResult(line, "INVALID", "Informe o nome", item));
            } else if (item.cpf() == null || !DocumentValidator.isValid(item.cpf(), DocumentType.CPF)) {
                lines.add(new PersonBatchLineResult(line, "INVALID", "CPF inválido", item));
            } else if (repository.existsByCpf(item.cpf().replaceAll("\\D", ""))) {
                lines.add(new PersonBatchLineResult(line, "DUPLICATE", "CPF já cadastrado", item));
            } else if (repository.existsByNameIgnoreCase(item.name().trim())) {
                lines.add(new PersonBatchLineResult(line, "DUPLICATE", "Pessoa já existe", item));
            } else {
                valid++;
                lines.add(new PersonBatchLineResult(line, "VALID", null, item));
            }
        }
        return new PersonBatchResponse(request.persons().size(), 0, 0, lines);
    }

    /**
     * Cria uma carga em lote. Processa linha a linha: linhas válidas são
     * criadas; duplicadas e inválidas são reportadas sem abortar as demais.
     */
    @Transactional
    public PersonBatchResponse createBatch(CreatePersonBatchRequest request) {
        List<PersonBatchLineResult> lines = new ArrayList<>();
        int imported = 0;
        for (int i = 0; i < request.persons().size(); i++) {
            CreatePersonBatchItem item = request.persons().get(i);
            int line = i + 2;
            if (item.name() == null || item.name().isBlank()) {
                lines.add(new PersonBatchLineResult(line, "INVALID", "Informe o nome", item));
            } else if (item.cpf() == null || !DocumentValidator.isValid(item.cpf(), DocumentType.CPF)) {
                lines.add(new PersonBatchLineResult(line, "INVALID", "CPF inválido", item));
            } else if (repository.existsByCpf(item.cpf().replaceAll("\\D", ""))) {
                lines.add(new PersonBatchLineResult(line, "DUPLICATE", "CPF já cadastrado", item));
            } else if (repository.existsByNameIgnoreCase(item.name().trim())) {
                lines.add(new PersonBatchLineResult(line, "DUPLICATE", "Pessoa já existe", item));
            } else {
                CreatePersonRequest createRequest = new CreatePersonRequest(
                        item.name().trim(), item.cpf().replaceAll("\\D", ""),
                        item.photoUrl(), null, null, item.role());
                PersonEntity entity = mapper.toEntity(createRequest);
                entity.setStatus(AthleteStatus.ACTIVE);
                repository.save(entity);
                imported++;
                lines.add(new PersonBatchLineResult(line, "IMPORTED", null, item));
            }
        }
        return new PersonBatchResponse(
                request.persons().size(), imported, request.persons().size() - imported, lines);
    }

    public PagedResponse<PersonResponse> findAll(int page, int size) {
        Page<PersonEntity> result = repository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name")));
        return new PagedResponse<>(
                mapper.toResponseList(result.getContent()),
                result.getTotalElements());
    }

    public PersonResponse findById(UUID id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    public PersonResponse update(UUID id, UpdatePersonRequest request) {
        PersonEntity entity = findEntityById(id);
        validateCpf(request.cpf(), id);
        mapper.updateEntity(entity, request);
        if (request.birthDate() != null) {
            entity.setBirthDate(request.birthDate());
        }
        if (request.gender() != null) {
            entity.setGender(request.gender());
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deactivate(UUID id) {
        PersonEntity entity = findEntityById(id);
        entity.setStatus(AthleteStatus.INACTIVE);
        repository.save(entity);
    }

    @Transactional
    public void reactivate(UUID id) {
        PersonEntity entity = findEntityById(id);
        entity.setStatus(AthleteStatus.ACTIVE);
        repository.save(entity);
    }

    private PersonEntity findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Override
    public void assertExists(UUID id) {
        findEntityById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public PersonInfo findPersonInfoById(UUID id) {
        PersonEntity entity = findEntityById(id);
        return new PersonInfo(
                entity.getId(),
                entity.getName(),
                entity.getPhotoUrl());
    }

    /**
     * Valida o CPF da pessoa: obrigatório, dígitos verificadores válidos e único.
     */
    private void validateCpf(String cpf, UUID currentId) {
        if (cpf == null || cpf.isBlank()) {
            throw new InvalidDocumentException("Informe o CPF da pessoa.");
        }
        if (!DocumentValidator.isValid(cpf, DocumentType.CPF)) {
            throw new InvalidDocumentException("CPF inválido.");
        }
        String normalized = cpf.replaceAll("\\D", "");
        boolean duplicate = currentId == null
                ? repository.existsByCpf(normalized)
                : repository.existsByCpfAndIdNot(normalized, currentId);
        if (duplicate) {
            throw new DuplicateDocumentException(normalized);
        }
    }

}
