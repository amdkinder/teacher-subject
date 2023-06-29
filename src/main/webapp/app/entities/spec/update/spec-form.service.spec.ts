import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../spec.test-samples';

import { SpecFormService } from './spec-form.service';

describe('Spec Form Service', () => {
  let service: SpecFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpecFormService);
  });

  describe('Service methods', () => {
    describe('createSpecFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSpecFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });

      it('passing ISpec should create a new form with FormGroup', () => {
        const formGroup = service.createSpecFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });
    });

    describe('getSpec', () => {
      it('should return NewSpec for default Spec initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSpecFormGroup(sampleWithNewData);

        const spec = service.getSpec(formGroup) as any;

        expect(spec).toMatchObject(sampleWithNewData);
      });

      it('should return NewSpec for empty Spec initial value', () => {
        const formGroup = service.createSpecFormGroup();

        const spec = service.getSpec(formGroup) as any;

        expect(spec).toMatchObject({});
      });

      it('should return ISpec', () => {
        const formGroup = service.createSpecFormGroup(sampleWithRequiredData);

        const spec = service.getSpec(formGroup) as any;

        expect(spec).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISpec should not enable id FormControl', () => {
        const formGroup = service.createSpecFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSpec should disable id FormControl', () => {
        const formGroup = service.createSpecFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
