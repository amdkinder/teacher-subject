export interface IStaff {
  id: number;
  name?: string | null;
  fromHours?: number | null;
  toHours?: number | null;
}

export type NewStaff = Omit<IStaff, 'id'> & { id: null };
