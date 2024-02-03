export interface IProduto {
    content:          Content[];
    pageable:         Pageable;
    totalPages:       number;
    totalElements:    number;
    last:             boolean;
    size:             number;
    number:           number;
    sort:             Sort;
    numberOfElements: number;
    first:            boolean;
    empty:            boolean;
}

export interface Produto{
    id: number;
    descricao: string;
    unidade: string;
    totalQuantidade: number;
}

export interface Content {
    id:        number;
    descricao: string;
    unidade:   string;
    totalQuantidade: number;
}

export interface Pageable {
    pageNumber: number;
    pageSize:   number;
    sort:       Sort;
    offset:     number;
    paged:      boolean;
    unpaged:    boolean;
}

export interface Sort {
    empty:    boolean;
    unsorted: boolean;
    sorted:   boolean;
}

export interface DatasProdutos {
  id:           number;
  idEstoque:    number;
  quantidade:   number;
  dataCompra:   string;
  dataValidade: string;
}
