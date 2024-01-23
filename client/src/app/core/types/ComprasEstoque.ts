export interface ICompraEstoque {
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

export interface CompraEstoque{
  id: number;
  precoUnitario: number;
  quantidade: number;
  idCompras: number;
  idEstoque: number;
}

export interface Content {
  id: number;
  precoUnitario: number;
  quantidade: number;
  idCompras: number;
  idEstoque: number;
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
