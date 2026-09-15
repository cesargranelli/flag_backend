package br.com.flagplatform.person.controller;

import br.com.flagplatform.person.dto.request.CreatePersonBatchRequest;
import br.com.flagplatform.person.dto.request.CreatePersonRequest;
import br.com.flagplatform.person.dto.request.UpdatePersonRequest;
import br.com.flagplatform.person.dto.response.PersonBatchResponse;
import br.com.flagplatform.person.dto.response.PersonResponse;
import br.com.flagplatform.person.service.PersonService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PersonController implements PersonApi {

    private final PersonService service;

    @Override
    public PersonResponse create(CreatePersonRequest request) {
        return service.create(request);
    }

    @Override
    public PersonBatchResponse validateBatch(CreatePersonBatchRequest request) {
        return service.validateBatch(request);
    }

    @Override
    public PersonBatchResponse createBatch(CreatePersonBatchRequest request) {
        return service.createBatch(request);
    }

    @Override
    public List<PersonResponse> findAll(int page, int size, HttpServletResponse response) {
        var result = service.findAll(page, size);
        response.setHeader("X-Total-Count", String.valueOf(result.total()));
        return result.items();
    }

    @Override
    public PersonResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public PersonResponse update(UUID id, UpdatePersonRequest request) {
        return service.update(id, request);
    }

    @Override
    public void deactivate(UUID id) {
        service.deactivate(id);
    }

    @Override
    public void reactivate(UUID id) {
        service.reactivate(id);
    }
}
