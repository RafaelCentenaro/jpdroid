----------
Jpdroid!
----------


O Jpdroid (Java Persistence For Android) é uma biblioteca para Android que propõe facilitar o desenvolvimento de aplicações que necessitem persistir objetos no banco de dados SQLite.


----------


Funcionalidades
---------
Operações básicas CRUD (Create, Read, Update e Delete).

#### <i class="icon-file"></i> Create

Inserção de novos registros.

#### <i class="icon-pencil"></i> Read

Recuperação de registros na base de dados.

#### <i class="icon-hdd"></i> Update

Atualização de registros na base de dados.

#### <i class="icon-trash"></i> Delete

Exclusão de registros da base de dados.



----------


Mapeamento de Objetos
---------------

Para realizar o mapeamento de objetos o Jpdroid possui um conjunto de anotações:

@Entity

Identifica classe como uma entidade do banco de dados.

@Column

Identifica atributo da classe como coluna no banco de dados.

@PrimaryKey

Identifica atributo da classe como chave primária da tabela.

@Foreing Key

Identifica atributo da classe como chave estrangeira.

@RelationClass

Identifica atributo como classe relacionada através de uma chave estrangeira.

@ViewColumn

Identifica atributo como campo auxiliar para visualização de um registro.

@Ignorable
Identifica atributo como "Ignorable", com esta marcação o atributo não será exportado para os arquivos.

@Dto
Identifica classe como objeto de transferencia de dados.

@DtoField
Identifica atributo da classe DTO como campo.
> **Importante:**

> Os atributos da classe DTO devem possuir exatamente o mesmo nome dos atributos da classe Entity.

#### <i class="icon-download"></i> Importação de Arquivos

O Jpdroid possibilita importar arquivos contendo script sql, estes arquivos podem estar armazenados no cartão SD ou na pasta Assets do projeto Android.

#### <i class="icon-upload"></i> Exportação de Arquivos

Após recuperar registros para um cursor ou para um objeto do tipo entity, o framework possibilita exportar os registros para os seguintes formatos:

- CSV

- XML

- JSON

----------


Aplicação Exemplo
-----------

No repositório existe um projeto exemplo chamado "PEDIDOJPDROID", este projeto propõe automatizar o processo de emissão de pedidos de venda.

Para testar as funcionalidades do framework o projeto exemplo possui:

- Cadastro de Pessoas

- Cadastro de Produtos

- Cadastro de Pedidos de Venda

- Importação de Arquivo

- Exportação de Arquivo
