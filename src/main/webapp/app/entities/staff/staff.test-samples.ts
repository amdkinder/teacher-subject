import { IStaff, NewStaff } from './staff.model';

export const sampleWithRequiredData: IStaff = {
  id: 25323,
};

export const sampleWithPartialData: IStaff = {
  id: 94486,
  fromHours: 16886,
  toHours: 81857,
};

export const sampleWithFullData: IStaff = {
  id: 99123,
  name: 'delectable',
  fromHours: 77756,
  toHours: 23476,
};

export const sampleWithNewData: NewStaff = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
