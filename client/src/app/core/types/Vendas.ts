export interface Vendas {
    _embedded: Embedded;
    _links:    VendasLinks;
    page:      Page;
}

export interface Embedded {
    vendasDTO: VendasDTO[];
}

export interface VendasDTO {
    id:                number;
    condicaoPagamento: string;
    dataVenda:         string;
    valorTotal:        number;
    idCliente:         number;
    _links?:           VendasDTOListLinks;
}

export interface Venda{
    id:                number;
    condicaoPagamento: string;
    dataVenda:         string;
    valorTotal:        number;
    idCliente:         number;
    links?:            string;
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