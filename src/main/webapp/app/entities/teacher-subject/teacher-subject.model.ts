import { ITeacher } from 'app/entities/teacher/teacher.model';
import { ISubject } from 'app/entities/subject/subject.model';

export interface ITeacherSubject {
  id: number;
  teacher?: Pick<ITeacher, 'id'> | null;
  subject?: Pick<ISubject, 'id'> | null;
}

export type NewTeacherSubject = Omit<ITeacherSubject, 'id'> & { id: null };
