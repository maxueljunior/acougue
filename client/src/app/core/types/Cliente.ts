export interface ICliente {
    content:          Content[];
    pageable:         Pageable;
    last:             boolean;
    totalElements:    number;
    totalPages:       number;
    first:            boolean;
    size:             number;
    number:           number;
    sort:             Sort;
    numberOfElements: number;
    empty:            boolean;
}

export interface Cliente{
    id:             number;
    nome:           string;
    sobrenome:      string;
    telefone:       string;
    sexo:           string;
    dataNascimento: string;
    endereco:       Endereco; 
}

export interface Content {
    id:             number;
    nome:           string;
    sobrenome:      string;
    telefone:       string;
    sexo:           string;
    dataNascimento: string;
    endereco:       Endereco;
}

export interface Endereco {
    rua:    string;
    bairro: string;
    numero: number;
}

export interface Pageable {
    pageNumber: number;
    pageSize:   number;
    sort:       Sort;
    offset:     number;
    unpaged:    boolean;
    paged:      boolean;
}

export interface Sort {
    empty:    boolean;
    sorted:   boolean;
    unsorted: boolean;
}