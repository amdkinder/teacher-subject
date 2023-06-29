import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITeacher, NewTeacher } from '../teacher.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeacher for edit and NewTeacherFormGroupInput for create.
 */
type TeacherFormGroupInput = ITeacher | PartialWithRequiredKeyOf<NewTeacher>;

type TeacherFormDefaults = Pick<NewTeacher, 'id'>;

type TeacherFormGroupContent = {
  id: FormControl<ITeacher['id'] | NewTeacher['id']>;
  fullnName: FormControl<ITeacher['fullnName']>;
  rank: FormControl<ITeacher['rank']>;
  staff: FormControl<ITeacher['staff']>;
  spec: FormControl<ITeacher['spec']>;
};

export type TeacherFormGroup = FormGroup<TeacherFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeacherFormService {
  createTeacherFormGroup(teacher: TeacherFormGroupInput = { id: null }): TeacherFormGroup {
    const teacherRawValue = {
      ...this.getFormDefaults(),
      ...teacher,
    };
    return new FormGroup<TeacherFormGroupContent>({
      id: new FormControl(
        { value: teacherRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fullnName: new FormControl(teacherRawValue.fullnName),
      rank: new FormControl(teacherRawValue.rank),
      staff: new FormControl(teacherRawValue.staff),
      spec: new FormControl(teacherRawValue.spec),
    });
  }

  getTeacher(form: TeacherFormGroup): ITeacher | NewTeacher {
    return form.getRawValue() as ITeacher | NewTeacher;
  }

  resetForm(form: TeacherFormGroup, teacher: TeacherFormGroupInput): void {
    const teacherRawValue = { ...this.getFormDefaults(), ...teacher };
    form.reset(
      {
        ...teacherRawValue,
        id: { value: teacherRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TeacherFormDefaults {
    return {
      id: null,
    };
  }
}
