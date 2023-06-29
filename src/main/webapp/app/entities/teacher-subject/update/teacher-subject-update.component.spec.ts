import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TeacherSubjectFormService } from './teacher-subject-form.service';
import { TeacherSubjectService } from '../service/teacher-subject.service';
import { ITeacherSubject } from '../teacher-subject.model';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { ISubject } from 'app/entities/subject/subject.model';
import { SubjectService } from 'app/entities/subject/service/subject.service';

import { TeacherSubjectUpdateComponent } from './teacher-subject-update.component';

describe('TeacherSubject Management Update Component', () => {
  let comp: TeacherSubjectUpdateComponent;
  let fixture: ComponentFixture<TeacherSubjectUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teacherSubjectFormService: TeacherSubjectFormService;
  let teacherSubjectService: TeacherSubjectService;
  let teacherService: TeacherService;
  let subjectService: SubjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TeacherSubjectUpdateComponent],
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
      .overrideTemplate(TeacherSubjectUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeacherSubjectUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teacherSubjectFormService = TestBed.inject(TeacherSubjectFormService);
    teacherSubjectService = TestBed.inject(TeacherSubjectService);
    teacherService = TestBed.inject(TeacherService);
    subjectService = TestBed.inject(SubjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Teacher query and add missing value', () => {
      const teacherSubject: ITeacherSubject = { id: 456 };
      const teacher: ITeacher = { id: 28084 };
      teacherSubject.teacher = teacher;

      const teacherCollection: ITeacher[] = [{ id: 41189 }];
      jest.spyOn(teacherService, 'query').mockReturnValue(of(new HttpResponse({ body: teacherCollection })));
      const additionalTeachers = [teacher];
      const expectedCollection: ITeacher[] = [...additionalTeachers, ...teacherCollection];
      jest.spyOn(teacherService, 'addTeacherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teacherSubject });
      comp.ngOnInit();

      expect(teacherService.query).toHaveBeenCalled();
      expect(teacherService.addTeacherToCollectionIfMissing).toHaveBeenCalledWith(
        teacherCollection,
        ...additionalTeachers.map(expect.objectContaining)
      );
      expect(comp.teachersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Subject query and add missing value', () => {
      const teacherSubject: ITeacherSubject = { id: 456 };
      const subject: ISubject = { id: 17158 };
      teacherSubject.subject = subject;

      const subjectCollection: ISubject[] = [{ id: 78334 }];
      jest.spyOn(subjectService, 'query').mockReturnValue(of(new HttpResponse({ body: subjectCollection })));
      const additionalSubjects = [subject];
      const expectedCollection: ISubject[] = [...additionalSubjects, ...subjectCollection];
      jest.spyOn(subjectService, 'addSubjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teacherSubject });
      comp.ngOnInit();

      expect(subjectService.query).toHaveBeenCalled();
      expect(subjectService.addSubjectToCollectionIfMissing).toHaveBeenCalledWith(
        subjectCollection,
        ...additionalSubjects.map(expect.objectContaining)
      );
      expect(comp.subjectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teacherSubject: ITeacherSubject = { id: 456 };
      const teacher: ITeacher = { id: 8776 };
      teacherSubject.teacher = teacher;
      const subject: ISubject = { id: 24362 };
      teacherSubject.subject = subject;

      activatedRoute.data = of({ teacherSubject });
      comp.ngOnInit();

      expect(comp.teachersSharedCollection).toContain(teacher);
      expect(comp.subjectsSharedCollection).toContain(subject);
      expect(comp.teacherSubject).toEqual(teacherSubject);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacherSubject>>();
      const teacherSubject = { id: 123 };
      jest.spyOn(teacherSubjectFormService, 'getTeacherSubject').mockReturnValue(teacherSubject);
      jest.spyOn(teacherSubjectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacherSubject });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacherSubject }));
      saveSubject.complete();

      // THEN
      expect(teacherSubjectFormService.getTeacherSubject).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teacherSubjectService.update).toHaveBeenCalledWith(expect.objectContaining(teacherSubject));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacherSubject>>();
      const teacherSubject = { id: 123 };
      jest.spyOn(teacherSubjectFormService, 'getTeacherSubject').mockReturnValue({ id: null });
      jest.spyOn(teacherSubjectService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacherSubject: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacherSubject }));
      saveSubject.complete();

      // THEN
      expect(teacherSubjectFormService.getTeacherSubject).toHaveBeenCalled();
      expect(teacherSubjectService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacherSubject>>();
      const teacherSubject = { id: 123 };
      jest.spyOn(teacherSubjectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacherSubject });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teacherSubjectService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTeacher', () => {
      it('Should forward to teacherService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teacherService, 'compareTeacher');
        comp.compareTeacher(entity, entity2);
        expect(teacherService.compareTeacher).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSubject', () => {
      it('Should forward to subjectService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(subjectService, 'compareSubject');
        comp.compareSubject(entity, entity2);
        expect(subjectService.compareSubject).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
