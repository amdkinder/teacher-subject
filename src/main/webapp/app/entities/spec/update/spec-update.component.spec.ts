import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SpecFormService } from './spec-form.service';
import { SpecService } from '../service/spec.service';
import { ISpec } from '../spec.model';

import { SpecUpdateComponent } from './spec-update.component';

describe('Spec Management Update Component', () => {
  let comp: SpecUpdateComponent;
  let fixture: ComponentFixture<SpecUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let specFormService: SpecFormService;
  let specService: SpecService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SpecUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SpecUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpecUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    specFormService = TestBed.inject(SpecFormService);
    specService = TestBed.inject(SpecService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const spec: ISpec = { id: 456 };

      activatedRoute.data = of({ spec });
      comp.ngOnInit();

      expect(comp.spec).toEqual(spec);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpec>>();
      const spec = { id: 123 };
      jest.spyOn(specFormService, 'getSpec').mockReturnValue(spec);
      jest.spyOn(specService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spec });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spec }));
      saveSubject.complete();

      // THEN
      expect(specFormService.getSpec).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(specService.update).toHaveBeenCalledWith(expect.objectContaining(spec));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpec>>();
      const spec = { id: 123 };
      jest.spyOn(specFormService, 'getSpec').mockReturnValue({ id: null });
      jest.spyOn(specService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spec: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spec }));
      saveSubject.complete();

      // THEN
      expect(specFormService.getSpec).toHaveBeenCalled();
      expect(specService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpec>>();
      const spec = { id: 123 };
      jest.spyOn(specService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spec });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(specService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
