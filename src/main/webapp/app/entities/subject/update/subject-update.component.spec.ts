import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SubjectFormService } from './subject-form.service';
import { SubjectService } from '../service/subject.service';
import { ISubject } from '../subject.model';
import { ISpec } from 'app/entities/spec/spec.model';
import { SpecService } from 'app/entities/spec/service/spec.service';

import { SubjectUpdateComponent } from './subject-update.component';

describe('Subject Management Update Component', () => {
  let comp: SubjectUpdateComponent;
  let fixture: ComponentFixture<SubjectUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let subjectFormService: SubjectFormService;
  let subjectService: SubjectService;
  let specService: SpecService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SubjectUpdateComponent],
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
      .overrideTemplate(SubjectUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubjectUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    subjectFormService = TestBed.inject(SubjectFormService);
    subjectService = TestBed.inject(SubjectService);
    specService = TestBed.inject(SpecService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Spec query and add missing value', () => {
      const subject: ISubject = { id: 456 };
      const spec: ISpec = { id: 72478 };
      subject.spec = spec;

      const specCollection: ISpec[] = [{ id: 41696 }];
      jest.spyOn(specService, 'query').mockReturnValue(of(new HttpResponse({ body: specCollection })));
      const additionalSpecs = [spec];
      const expectedCollection: ISpec[] = [...additionalSpecs, ...specCollection];
      jest.spyOn(specService, 'addSpecToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      expect(specService.query).toHaveBeenCalled();
      expect(specService.addSpecToCollectionIfMissing).toHaveBeenCalledWith(
        specCollection,
        ...additionalSpecs.map(expect.objectContaining)
      );
      expect(comp.specsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const subject: ISubject = { id: 456 };
      const spec: ISpec = { id: 54788 };
      subject.spec = spec;

      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      expect(comp.specsSharedCollection).toContain(spec);
      expect(comp.subject).toEqual(subject);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubject>>();
      const subject = { id: 123 };
      jest.spyOn(subjectFormService, 'getSubject').mockReturnValue(subject);
      jest.spyOn(subjectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subject }));
      saveSubject.complete();

      // THEN
      expect(subjectFormService.getSubject).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(subjectService.update).toHaveBeenCalledWith(expect.objectContaining(subject));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubject>>();
      const subject = { id: 123 };
      jest.spyOn(subjectFormService, 'getSubject').mockReturnValue({ id: null });
      jest.spyOn(subjectService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subject: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subject }));
      saveSubject.complete();

      // THEN
      expect(subjectFormService.getSubject).toHaveBeenCalled();
      expect(subjectService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubject>>();
      const subject = { id: 123 };
      jest.spyOn(subjectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(subjectService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSpec', () => {
      it('Should forward to specService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(specService, 'compareSpec');
        comp.compareSpec(entity, entity2);
        expect(specService.compareSpec).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
