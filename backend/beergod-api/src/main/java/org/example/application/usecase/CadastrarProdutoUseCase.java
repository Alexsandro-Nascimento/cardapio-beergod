package org.example.application.usecase;

import org.example.application.dto.CriarProdutoDTO;
import org.example.application.dto.ProdutoResponseDTO;
import org.example.domain.model.CategoriaProduto;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;
import org.example.domain.model.Bebida;


public class CadastrarProdutoUseCase {
    private final ProdutoRepository produtoRepository;

    public CadastrarProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ProdutoResponseDTO executar(CriarProdutoDTO dto){
        CategoriaProduto categoriaEnum = CategoriaProduto.valueOf(dto.categoria().toUpperCase());

        Produto produtoParaSalvar = new Bebida(
                null,
                dto.nome(),
                dto.descricao(),
                dto.preco(),
                dto.tamanhoML(),
                dto.marca()
        );

        Produto produtoSalvo = produtoRepository.salvar(produtoParaSalvar);
        Bebida bebidaSalva = (Bebida) produtoSalvo;

        return new ProdutoResponseDTO(
                bebidaSalva.getId(),
                bebidaSalva.getNome(),
                bebidaSalva.getDescricao(),
                bebidaSalva.getPreco(),
                bebidaSalva.getCategoria().name(),
                bebidaSalva.getTamanhoML(),
                bebidaSalva.getMarca()
        );
    }
}
