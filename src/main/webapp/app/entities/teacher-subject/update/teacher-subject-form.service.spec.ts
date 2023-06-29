import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../teacher-subject.test-samples';

import { TeacherSubjectFormService } from './teacher-subject-form.service';

describe('TeacherSubject Form Service', () => {
  let service: TeacherSubjectFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeacherSubjectFormService);
  });

  describe('Service methods', () => {
    describe('createTeacherSubjectFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeacherSubjectFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            teacher: expect.any(Object),
            subject: expect.any(Object),
          })
        );
      });

      it('passing ITeacherSubject should create a new form with FormGroup', () => {
        const formGroup = service.createTeacherSubjectFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            teacher: expect.any(Object),
            subject: expect.any(Object),
          })
        );
      });
    });

    describe('getTeacherSubject', () => {
      it('should return NewTeacherSubject for default TeacherSubject initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTeacherSubjectFormGroup(sampleWithNewData);

        const teacherSubject = service.getTeacherSubject(formGroup) as any;

        expect(teacherSubject).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeacherSubject for empty TeacherSubject initial value', () => {
        const formGroup = service.createTeacherSubjectFormGroup();

        const teacherSubject = service.getTeacherSubject(formGroup) as any;

        expect(teacherSubject).toMatchObject({});
      });

      it('should return ITeacherSubject', () => {
        const formGroup = service.createTeacherSubjectFormGroup(sampleWithRequiredData);

        const teacherSubject = service.getTeacherSubject(formGroup) as any;

        expect(teacherSubject).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeacherSubject should not enable id FormControl', () => {
        const formGroup = service.createTeacherSubjectFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeacherSubject should disable id FormControl', () => {
        const formGroup = service.createTeacherSubjectFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
