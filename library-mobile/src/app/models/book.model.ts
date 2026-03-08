import { Author } from './author.model';
import { Publisher } from './publisher.model';

export interface Book {
  id?: number;
  isbn?: string;
  name?: string;
  publishYear?: string;
  copies?: number;
  cover?: string | null;
  publisher?: Publisher;
  authors?: Author[];
}
