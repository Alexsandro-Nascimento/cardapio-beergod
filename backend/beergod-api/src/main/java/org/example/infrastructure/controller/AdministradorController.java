package org.example.infrastructure.controller;

import org.example.application.dto.CriarAdministradorDTO;
import org.example.application.dto.AdministradorResponseDTO;
import org.example.application.usecase.CriarAdministradorUseCase;
import org.example.domain.model.Administrador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administradores")
public class AdministradorController {
    private final CriarAdministradorUseCase criarAdministradorUseCase;

    public AdministradorController(CriarAdministradorUseCase criarAdministradorUseCase) {
        this.criarAdministradorUseCase = criarAdministradorUseCase;
    }

    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> criar(@RequestBody CriarAdministradorDTO dto) {
        Administrador adminCriado = criarAdministradorUseCase.criarAdministrador(dto);

        AdministradorResponseDTO response = new AdministradorResponseDTO(
                adminCriado.getId(),
                adminCriado.getNome(),
                adminCriado.getEmail(),
                adminCriado.isAtivo()
        );

    return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

