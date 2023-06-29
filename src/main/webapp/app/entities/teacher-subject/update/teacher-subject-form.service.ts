import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITeacherSubject, NewTeacherSubject } from '../teacher-subject.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeacherSubject for edit and NewTeacherSubjectFormGroupInput for create.
 */
type TeacherSubjectFormGroupInput = ITeacherSubject | PartialWithRequiredKeyOf<NewTeacherSubject>;

type TeacherSubjectFormDefaults = Pick<NewTeacherSubject, 'id'>;

type TeacherSubjectFormGroupContent = {
  id: FormControl<ITeacherSubject['id'] | NewTeacherSubject['id']>;
  teacher: FormControl<ITeacherSubject['teacher']>;
  subject: FormControl<ITeacherSubject['subject']>;
};

export type TeacherSubjectFormGroup = FormGroup<TeacherSubjectFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeacherSubjectFormService {
  createTeacherSubjectFormGroup(teacherSubject: TeacherSubjectFormGroupInput = { id: null }): TeacherSubjectFormGroup {
    const teacherSubjectRawValue = {
      ...this.getFormDefaults(),
      ...teacherSubject,
    };
    return new FormGroup<TeacherSubjectFormGroupContent>({
      id: new FormControl(
        { value: teacherSubjectRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      teacher: new FormControl(teacherSubjectRawValue.teacher),
      subject: new FormControl(teacherSubjectRawValue.subject),
    });
  }

  getTeacherSubject(form: TeacherSubjectFormGroup): ITeacherSubject | NewTeacherSubject {
    return form.getRawValue() as ITeacherSubject | NewTeacherSubject;
  }

  resetForm(form: TeacherSubjectFormGroup, teacherSubject: TeacherSubjectFormGroupInput): void {
    const teacherSubjectRawValue = { ...this.getFormDefaults(), ...teacherSubject };
    form.reset(
      {
        ...teacherSubjectRawValue,
        id: { value: teacherSubjectRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TeacherSubjectFormDefaults {
    return {
      id: null,
    };
  }
}
