import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISpec, NewSpec } from '../spec.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISpec for edit and NewSpecFormGroupInput for create.
 */
type SpecFormGroupInput = ISpec | PartialWithRequiredKeyOf<NewSpec>;

type SpecFormDefaults = Pick<NewSpec, 'id'>;

type SpecFormGroupContent = {
  id: FormControl<ISpec['id'] | NewSpec['id']>;
  name: FormControl<ISpec['name']>;
};

export type SpecFormGroup = FormGroup<SpecFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SpecFormService {
  createSpecFormGroup(spec: SpecFormGroupInput = { id: null }): SpecFormGroup {
    const specRawValue = {
      ...this.getFormDefaults(),
      ...spec,
    };
    return new FormGroup<SpecFormGroupContent>({
      id: new FormControl(
        { value: specRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(specRawValue.name),
    });
  }

  getSpec(form: SpecFormGroup): ISpec | NewSpec {
    return form.getRawValue() as ISpec | NewSpec;
  }

  resetForm(form: SpecFormGroup, spec: SpecFormGroupInput): void {
    const specRawValue = { ...this.getFormDefaults(), ...spec };
    form.reset(
      {
        ...specRawValue,
        id: { value: specRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SpecFormDefaults {
    return {
      id: null,
    };
  }
}
