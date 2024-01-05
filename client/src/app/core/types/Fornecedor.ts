export interface IFornecedor {
  content:          Content[];
  pageable:         Pageable;
  last:             boolean;
  totalPages:       number;
  totalElements:    number;
  size:             number;
  number:           number;
  sort:             Sort;
  first:            boolean;
  numberOfElements: number;
  empty:            boolean;
}

export interface Fornecedor{
  id:          number;
  razaoSocial: string;
  cnpj:        string;
  nomeContato: string;
  telefone:    string;
}

export interface Content {
  id:          number;
  razaoSocial: string;
  cnpj:        string;
  nomeContato: string;
  telefone:    string;
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
  sorted:   boolean;
  unsorted: boolean;
}
