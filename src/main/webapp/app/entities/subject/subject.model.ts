import { ISpec } from 'app/entities/spec/spec.model';

export interface ISubject {
  id: number;
  name?: string | null;
  hours?: number | null;
  inUse?: boolean | null;
  spec?: Pick<ISpec, 'id'> | null;
}

export type NewSubject = Omit<ISubject, 'id'> & { id: null };
