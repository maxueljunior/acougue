import { Cliente } from "./Cliente";

export interface Vendas {
    _embedded: Embedded;
    _links:    VendasLinks;
    page:      Page;
}

export interface Embedded {
    vendasDTOList: VendasDTO[];
}

export interface VendasDTO {
    id:                number;
    condicaoPagamento: string;
    dataVenda:         string;
    valorTotal:        number;
    cliente:           Cliente;
    _links?:           VendasDTOListLinks;
}

export interface Venda{
    id:                number;
    condicaoPagamento: string;
    dataVenda:         string;
    valorTotal:        number;
    cliente:           Cliente;
    _links?:            string;
}

export interface VendasDTOListLinks {
    download: Self;
}

export interface Self {
    href: string;
}

export interface VendasLinks {
    self: Self;
}

export interface Page {
    size:          number;
    totalElements: number;
    totalPages:    number;
    number:        number;
}