package org.example.infrastructure.persistence.inmemory;

import org.example.domain.repository.ProdutoRepository;
import org.example.domain.model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProdutoRepository implements ProdutoRepository {
    private final List<Produto> bancoEmMemoria = new ArrayList<>();

    @Override
    public Produto salvar(Produto produto){
        if(produto.getId() == null){
            produto.setId((long) (bancoEmMemoria.size() + 1));
        }else{
            deletar(produto.getId());
        }
        bancoEmMemoria.add(produto);
        return produto;
    }

    @Override
    public Optional<Produto> buscarPorId(Long id){
        return bancoEmMemoria.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Produto> buscarTodos(){
        return new ArrayList<>(bancoEmMemoria);
    }

    @Override
    public void deletar(Long id){
        bancoEmMemoria.removeIf(p -> p.getId().equals(id));
    }
}
