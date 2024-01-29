import { Fornecedor } from "./Fornecedor";

export interface ICompras {
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

export interface Compras{
  id: number;
  valorTotal: number;
  fornecedor: Fornecedor;
  data: Date
}

export interface Upload{
  fileName:    string;
  fileType:    string;
  size:        number;
  downloadUrl: string;
}

export interface Content {
  id: number;
  valorTotal: number;
  fornecedor: Fornecedor;
  data: Date
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
