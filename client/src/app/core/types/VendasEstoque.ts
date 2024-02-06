import { DatasProdutos, Produto } from "./Produto";

export interface VendasEstoque {
    content:          Content[];
    pageable:         Pageable;
    last:             boolean;
    totalElements:    number;
    totalPages:       number;
    size:             number;
    number:           number;
    sort:             Sort;
    first:            boolean;
    numberOfElements: number;
    empty:            boolean;
}

export interface Content {
    id:            number;
    quantidade:    number;
    valorUnitario: number;
    idEstoque:     number;
    idVendas:      number;
}

export interface VendaEstoque{
    id:            number;
    quantidade:    number;
    valorUnitario: number;
    idEstoque:     number;
    idVendas:      number;
}

export interface InsertVendaEstoque{
    quantidade:    number;
    valorUnitario: number;
    idEstoque:     number;
    idVendas:      number;
    dataEstoque:   string; 
}

export interface VendaEstoqueTable{
    id: number;
    produto: Produto;
    quantidade: number;
    valorUnitario: number;
    dataEstoque: DatasProdutos;
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
