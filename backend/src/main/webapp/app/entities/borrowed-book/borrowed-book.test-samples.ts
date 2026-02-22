import dayjs from 'dayjs/esm';

import { IBorrowedBook, NewBorrowedBook } from './borrowed-book.model';

export const sampleWithRequiredData: IBorrowedBook = {
  id: 27089,
};

export const sampleWithPartialData: IBorrowedBook = {
  id: 6307,
};

export const sampleWithFullData: IBorrowedBook = {
  id: 29509,
  borrowDate: dayjs('2026-02-21'),
};

export const sampleWithNewData: NewBorrowedBook = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
