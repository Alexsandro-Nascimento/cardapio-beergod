package org.example.application.usecase;

import org.example.application.dto.ProdutoResponseDTO;
import org.example.domain.model.Bebida;
import org.example.domain.model.Categoria;
import org.example.domain.repository.CategoriaRepository;
import org.example.domain.repository.ProdutoRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListarProdutosUseCase {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ListarProdutosUseCase(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProdutoResponseDTO> executar(boolean apenasAtivos) {
        Map<Long, String> nomesCategorias = categoriaRepository.listarTodos(false).stream()
                .collect(Collectors.toMap(Categoria::getId, Categoria::getNome));

        return produtoRepository.listarTodos(apenasAtivos).stream()
                .map(p -> {
                    Integer tamanhoML = (p instanceof Bebida b) ? b.getTamanhoML() : null;
                    String marca = (p instanceof Bebida b) ? b.getMarca() : null;
                    String tipo = (p instanceof Bebida) ? "BEBIDA" : "PRODUTO";

                    return new ProdutoResponseDTO(
                            p.getId(),
                            p.getNome(),
                            p.getDescricao(),
                            p.getPreco(),
                            p.getImagemUrl(),
                            p.isAtivo(),
                            p.getCategoriaId(),
                            nomesCategorias.getOrDefault(p.getCategoriaId(), "Sem Categoria"),
                            tipo,
                            tamanhoML,
                            marca
                    );
                })
                .toList();
    }
}