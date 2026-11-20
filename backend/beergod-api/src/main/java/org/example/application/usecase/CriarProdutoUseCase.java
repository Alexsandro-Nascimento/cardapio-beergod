package org.example.application.usecase;

import org.example.application.dto.ProdutoRequestDTO;
import org.example.application.dto.ProdutoResponseDTO;
import org.example.application.security.ContextoAutenticacao;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.Categoria;
import org.example.domain.model.PerfilAdministrador;
import org.example.domain.model.Produto;
import org.example.domain.repository.CategoriaRepository;
import org.example.domain.repository.ProdutoRepository;

public class CriarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ContextoAutenticacao contextoAutenticacao;

    public CriarProdutoUseCase(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this(produtoRepository, categoriaRepository, null);
    }

    public CriarProdutoUseCase(ProdutoRepository produtoRepository,
                               CategoriaRepository categoriaRepository,
                               ContextoAutenticacao contextoAutenticacao) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.contextoAutenticacao = contextoAutenticacao;
    }

    public ProdutoResponseDTO executar(ProdutoRequestDTO dto) {
        boolean isBebida = dto.tamanhoML() != null || (dto.marca() != null && !dto.marca().isBlank());
        validarPermissaoItem(isBebida);

        Categoria categoria = categoriaRepository.buscarPorId(dto.categoriaId())
                .orElseThrow(() -> new RegraNegocioException("Categoria informada não existe!"));

        Produto novo = isBebida
                ? new Bebida(dto.nome(), dto.descricao(), dto.preco(), dto.imagemUrl(), dto.categoriaId(),
                dto.tamanhoML() != null ? dto.tamanhoML() : 0, dto.marca())
                : new Produto(dto.nome(), dto.descricao(), dto.preco(), dto.imagemUrl(), dto.categoriaId());

        Produto salvo = produtoRepository.salvar(novo);

        Integer tamanhoML = (salvo instanceof Bebida b) ? b.getTamanhoML() : null;
        String marca = (salvo instanceof Bebida b) ? b.getMarca() : null;
        String tipo = (salvo instanceof Bebida) ? "BEBIDA" : "PRODUTO";

        return new ProdutoResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getDescricao(),
                salvo.getPreco(),
                salvo.getImagemUrl(),
                salvo.isAtivo(),
                salvo.getCategoriaId(),
                categoria.getNome(),
                tipo,
                tamanhoML,
                marca
        );
    }

    private void validarPermissaoItem(boolean isBebida) {
        if (contextoAutenticacao == null) return;
        contextoAutenticacao.getUsuarioLogado().ifPresent(admin -> {
            PerfilAdministrador perfil = admin.getPerfil();
            if (perfil == PerfilAdministrador.FUNCIONARIO) {
                throw new RegraNegocioException("Seu perfil possui apenas permissão de visualização.");
            }
            if (perfil == PerfilAdministrador.ADMIN_BEBIDAS && !isBebida) {
                throw new RegraNegocioException("Seu perfil permite gerenciar apenas Bebidas.");
            }
            if (perfil == PerfilAdministrador.ADMIN_COZINHA && isBebida) {
                throw new RegraNegocioException("Seu perfil permite gerenciar apenas itens da Cozinha.");
            }
        });
    }
}