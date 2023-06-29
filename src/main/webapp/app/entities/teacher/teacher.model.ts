import { IStaff } from 'app/entities/staff/staff.model';
import { ISpec } from 'app/entities/spec/spec.model';
import { AcademicRank } from 'app/entities/enumerations/academic-rank.model';

export interface ITeacher {
  id: number;
  fullnName?: string | null;
  rank?: keyof typeof AcademicRank | null;
  staff?: Pick<IStaff, 'id'> | null;
  spec?: Pick<ISpec, 'id'> | null;
}

export type NewTeacher = Omit<ITeacher, 'id'> & { id: null };
