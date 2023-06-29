import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TeacherFormService } from './teacher-form.service';
import { TeacherService } from '../service/teacher.service';
import { ITeacher } from '../teacher.model';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';
import { ISpec } from 'app/entities/spec/spec.model';
import { SpecService } from 'app/entities/spec/service/spec.service';

import { TeacherUpdateComponent } from './teacher-update.component';

describe('Teacher Management Update Component', () => {
  let comp: TeacherUpdateComponent;
  let fixture: ComponentFixture<TeacherUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teacherFormService: TeacherFormService;
  let teacherService: TeacherService;
  let staffService: StaffService;
  let specService: SpecService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TeacherUpdateComponent],
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
      .overrideTemplate(TeacherUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeacherUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teacherFormService = TestBed.inject(TeacherFormService);
    teacherService = TestBed.inject(TeacherService);
    staffService = TestBed.inject(StaffService);
    specService = TestBed.inject(SpecService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Staff query and add missing value', () => {
      const teacher: ITeacher = { id: 456 };
      const staff: IStaff = { id: 71686 };
      teacher.staff = staff;

      const staffCollection: IStaff[] = [{ id: 93327 }];
      jest.spyOn(staffService, 'query').mockReturnValue(of(new HttpResponse({ body: staffCollection })));
      const additionalStaff = [staff];
      const expectedCollection: IStaff[] = [...additionalStaff, ...staffCollection];
      jest.spyOn(staffService, 'addStaffToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(staffService.query).toHaveBeenCalled();
      expect(staffService.addStaffToCollectionIfMissing).toHaveBeenCalledWith(
        staffCollection,
        ...additionalStaff.map(expect.objectContaining)
      );
      expect(comp.staffSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Spec query and add missing value', () => {
      const teacher: ITeacher = { id: 456 };
      const spec: ISpec = { id: 72854 };
      teacher.spec = spec;

      const specCollection: ISpec[] = [{ id: 68452 }];
      jest.spyOn(specService, 'query').mockReturnValue(of(new HttpResponse({ body: specCollection })));
      const additionalSpecs = [spec];
      const expectedCollection: ISpec[] = [...additionalSpecs, ...specCollection];
      jest.spyOn(specService, 'addSpecToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(specService.query).toHaveBeenCalled();
      expect(specService.addSpecToCollectionIfMissing).toHaveBeenCalledWith(
        specCollection,
        ...additionalSpecs.map(expect.objectContaining)
      );
      expect(comp.specsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teacher: ITeacher = { id: 456 };
      const staff: IStaff = { id: 62511 };
      teacher.staff = staff;
      const spec: ISpec = { id: 27070 };
      teacher.spec = spec;

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(comp.staffSharedCollection).toContain(staff);
      expect(comp.specsSharedCollection).toContain(spec);
      expect(comp.teacher).toEqual(teacher);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 123 };
      jest.spyOn(teacherFormService, 'getTeacher').mockReturnValue(teacher);
      jest.spyOn(teacherService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacher }));
      saveSubject.complete();

      // THEN
      expect(teacherFormService.getTeacher).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teacherService.update).toHaveBeenCalledWith(expect.objectContaining(teacher));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 123 };
      jest.spyOn(teacherFormService, 'getTeacher').mockReturnValue({ id: null });
      jest.spyOn(teacherService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacher }));
      saveSubject.complete();

      // THEN
      expect(teacherFormService.getTeacher).toHaveBeenCalled();
      expect(teacherService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 123 };
      jest.spyOn(teacherService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teacherService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStaff', () => {
      it('Should forward to staffService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(staffService, 'compareStaff');
        comp.compareStaff(entity, entity2);
        expect(staffService.compareStaff).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
