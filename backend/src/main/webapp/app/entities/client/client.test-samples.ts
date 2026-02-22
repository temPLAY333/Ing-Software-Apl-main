import { IClient, NewClient } from './client.model';

export const sampleWithRequiredData: IClient = {
  id: 16289,
  firstName: 'Sadye',
  lastName: 'Macejkovic',
};

export const sampleWithPartialData: IClient = {
  id: 8997,
  firstName: 'Alexzander',
  lastName: 'Stark',
  email: 'Damaris.Littel@yahoo.com',
  address: 'beside yum dimly',
  phone: '558.532.1602',
};

export const sampleWithFullData: IClient = {
  id: 31496,
  firstName: 'Zelda',
  lastName: 'Herman',
  email: 'Ines75@hotmail.com',
  address: 'off',
  phone: '687-693-9315 x6726',
};

export const sampleWithNewData: NewClient = {
  firstName: 'Ida',
  lastName: 'Smitham',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
