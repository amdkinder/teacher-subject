jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { TeacherSubjectService } from '../service/teacher-subject.service';

import { TeacherSubjectDeleteDialogComponent } from './teacher-subject-delete-dialog.component';

describe('TeacherSubject Management Delete Component', () => {
  let comp: TeacherSubjectDeleteDialogComponent;
  let fixture: ComponentFixture<TeacherSubjectDeleteDialogComponent>;
  let service: TeacherSubjectService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TeacherSubjectDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(TeacherSubjectDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TeacherSubjectDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TeacherSubjectService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
