import { Component } from '@angular/core';
import { MatPaginator, MatPaginatorIntl } from '@angular/material/paginator';

export class CustomPaginatorIntl extends MatPaginatorIntl {
  override itemsPerPageLabel = 'Itens por página:';
  override nextPageLabel = 'Próxima Página';
  override previousPageLabel = 'Página Anterior';
  override firstPageLabel = 'Primeira Página';
  override lastPageLabel = 'Última Página';

  override getRangeLabel = (page: number, pageSize: number, length: number): string => {
    if (length === 0 || pageSize === 0) {
      return `0 de ${length}`;
    }

    length = Math.max(length, 0);

    const startIndex = page * pageSize;

    // Se o índice inicial for maior ou igual ao comprimento, exibimos apenas o comprimento.
    if (startIndex >= length) {
      return `${length} de ${length}`;
    }

    const endIndex = startIndex < length ?
      Math.min(startIndex + pageSize, length) :
      startIndex + pageSize;

    return `${startIndex + 1} - ${endIndex} de ${length}`;
  };
}
