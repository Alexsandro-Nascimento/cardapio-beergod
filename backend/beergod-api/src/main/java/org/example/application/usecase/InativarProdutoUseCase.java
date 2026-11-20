package org.example.application.usecase;

import org.example.application.security.ContextoAutenticacao;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.PerfilAdministrador;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;

public class InativarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final ContextoAutenticacao contextoAutenticacao;

    public InativarProdutoUseCase(ProdutoRepository produtoRepository) {
        this(produtoRepository, null);
    }

    public InativarProdutoUseCase(ProdutoRepository produtoRepository,
                                  ContextoAutenticacao contextoAutenticacao) {
        this.produtoRepository = produtoRepository;
        this.contextoAutenticacao = contextoAutenticacao;
    }

    public void executar(Long id) {
        Produto produto = produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado!"));

        if (contextoAutenticacao != null) {
            boolean isBebida = produto instanceof Bebida;
            contextoAutenticacao.getUsuarioLogado().ifPresent(admin -> {
                PerfilAdministrador perfil = admin.getPerfil();
                if (perfil == PerfilAdministrador.FUNCIONARIO) {
                    throw new RegraNegocioException("Seu perfil possui apenas permissão de visualização.");
                }
                if (perfil == PerfilAdministrador.ADMIN_BEBIDAS && !isBebida) {
                    throw new RegraNegocioException("Seu perfil permite inativar apenas Bebidas.");
                }
                if (perfil == PerfilAdministrador.ADMIN_COZINHA && isBebida) {
                    throw new RegraNegocioException("Seu perfil permite inativar apenas itens da Cozinha.");
                }
            });
        }

        produto.inativar();
        produtoRepository.atualizar(produto);
    }
}