import { AcademicRank } from 'app/entities/enumerations/academic-rank.model';

import { ITeacher, NewTeacher } from './teacher.model';

export const sampleWithRequiredData: ITeacher = {
  id: 95344,
};

export const sampleWithPartialData: ITeacher = {
  id: 32290,
};

export const sampleWithFullData: ITeacher = {
  id: 80701,
  fullnName: 'Rover up Nihonium',
  rank: 'HEAD_TEACHER',
};

export const sampleWithNewData: NewTeacher = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
