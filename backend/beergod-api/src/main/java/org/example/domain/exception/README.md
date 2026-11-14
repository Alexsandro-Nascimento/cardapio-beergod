# 🚨 Pacote de Exceções (Exceptions)

Este pacote é o "segurança" do nosso sistema e guarda as regras de negócio.
Aqui ficarão as classes de erro personalizadas (como `PrecoInvalidoException` ou `NomeVazioException`). Se alguém tentar burlar as regras do Beergod (como colocar um preço negativo), é este pacote que vai barrar a operação.