import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TeacherSubjectFormService, TeacherSubjectFormGroup } from './teacher-subject-form.service';
import { ITeacherSubject } from '../teacher-subject.model';
import { TeacherSubjectService } from '../service/teacher-subject.service';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { ISubject } from 'app/entities/subject/subject.model';
import { SubjectService } from 'app/entities/subject/service/subject.service';

@Component({
  standalone: true,
  selector: 'jhi-teacher-subject-update',
  templateUrl: './teacher-subject-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeacherSubjectUpdateComponent implements OnInit {
  isSaving = false;
  teacherSubject: ITeacherSubject | null = null;

  teachersSharedCollection: ITeacher[] = [];
  subjectsSharedCollection: ISubject[] = [];

  editForm: TeacherSubjectFormGroup = this.teacherSubjectFormService.createTeacherSubjectFormGroup();

  constructor(
    protected teacherSubjectService: TeacherSubjectService,
    protected teacherSubjectFormService: TeacherSubjectFormService,
    protected teacherService: TeacherService,
    protected subjectService: SubjectService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTeacher = (o1: ITeacher | null, o2: ITeacher | null): boolean => this.teacherService.compareTeacher(o1, o2);

  compareSubject = (o1: ISubject | null, o2: ISubject | null): boolean => this.subjectService.compareSubject(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teacherSubject }) => {
      this.teacherSubject = teacherSubject;
      if (teacherSubject) {
        this.updateForm(teacherSubject);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teacherSubject = this.teacherSubjectFormService.getTeacherSubject(this.editForm);
    if (teacherSubject.id !== null) {
      this.subscribeToSaveResponse(this.teacherSubjectService.update(teacherSubject));
    } else {
      this.subscribeToSaveResponse(this.teacherSubjectService.create(teacherSubject));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeacherSubject>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(teacherSubject: ITeacherSubject): void {
    this.teacherSubject = teacherSubject;
    this.teacherSubjectFormService.resetForm(this.editForm, teacherSubject);

    this.teachersSharedCollection = this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(
      this.teachersSharedCollection,
      teacherSubject.teacher
    );
    this.subjectsSharedCollection = this.subjectService.addSubjectToCollectionIfMissing<ISubject>(
      this.subjectsSharedCollection,
      teacherSubject.subject
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teacherService
      .query()
      .pipe(map((res: HttpResponse<ITeacher[]>) => res.body ?? []))
      .pipe(
        map((teachers: ITeacher[]) => this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(teachers, this.teacherSubject?.teacher))
      )
      .subscribe((teachers: ITeacher[]) => (this.teachersSharedCollection = teachers));

    this.subjectService
      .query()
      .pipe(map((res: HttpResponse<ISubject[]>) => res.body ?? []))
      .pipe(
        map((subjects: ISubject[]) => this.subjectService.addSubjectToCollectionIfMissing<ISubject>(subjects, this.teacherSubject?.subject))
      )
      .subscribe((subjects: ISubject[]) => (this.subjectsSharedCollection = subjects));
  }
}
