import { ISubject, NewSubject } from './subject.model';

export const sampleWithRequiredData: ISubject = {
  id: 42013,
};

export const sampleWithPartialData: ISubject = {
  id: 15169,
  hours: 91727,
  inUse: false,
};

export const sampleWithFullData: ISubject = {
  id: 14027,
  name: 'Price hardware Southeast',
  hours: 37420,
  inUse: true,
};

export const sampleWithNewData: NewSubject = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
