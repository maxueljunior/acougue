import { Fornecedor } from "./Fornecedor";

export interface Upload{
  fileName:    string;
  fileType:    string;
  size:        number;
  downloadUrl: string;
}

export interface ICompras {
  _embedded: Embedded;
  _links:    ComprasLinks;
  page:      Page;
}

export interface Embedded {
  comprasDTOList: Compras[];
}

export interface Compras {
  id:         number;
  valorTotal: number;
  fornecedor: Fornecedor;
  data:       string;
  _links?:    LinkDownload;
}

export interface Compra{
  id:         number;
  valorTotal: number;
  fornecedor: Fornecedor;
  data:       string;
  links?:     string;
}

export interface LinkDownload {
  download?: First;
}

export interface First {
  href: string;
}

export interface ComprasLinks {
  first: First;
  self:  First;
  next:  First;
  last:  First;
}

export interface Page {
  size:          number;
  totalElements: number;
  totalPages:    number;
  number:        number;
}
