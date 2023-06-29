import { ISpec, NewSpec } from './spec.model';

export const sampleWithRequiredData: ISpec = {
  id: 32046,
};

export const sampleWithPartialData: ISpec = {
  id: 6682,
  name: 'Obie Cruiser weber',
};

export const sampleWithFullData: ISpec = {
  id: 26213,
  name: 'navigate male',
};

export const sampleWithNewData: NewSpec = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
