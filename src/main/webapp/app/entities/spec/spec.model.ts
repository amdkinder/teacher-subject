export interface ISpec {
  id: number;
  name?: string | null;
}

export type NewSpec = Omit<ISpec, 'id'> & { id: null };
