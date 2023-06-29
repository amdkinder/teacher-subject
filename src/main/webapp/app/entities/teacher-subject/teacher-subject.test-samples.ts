import { ITeacherSubject, NewTeacherSubject } from './teacher-subject.model';

export const sampleWithRequiredData: ITeacherSubject = {
  id: 12512,
};

export const sampleWithPartialData: ITeacherSubject = {
  id: 93206,
};

export const sampleWithFullData: ITeacherSubject = {
  id: 89595,
};

export const sampleWithNewData: NewTeacherSubject = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
