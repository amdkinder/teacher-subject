import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SubjectFormService, SubjectFormGroup } from './subject-form.service';
import { ISubject } from '../subject.model';
import { SubjectService } from '../service/subject.service';
import { ISpec } from 'app/entities/spec/spec.model';
import { SpecService } from 'app/entities/spec/service/spec.service';

@Component({
  standalone: true,
  selector: 'jhi-subject-update',
  templateUrl: './subject-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SubjectUpdateComponent implements OnInit {
  isSaving = false;
  subject: ISubject | null = null;

  specsSharedCollection: ISpec[] = [];

  editForm: SubjectFormGroup = this.subjectFormService.createSubjectFormGroup();

  constructor(
    protected subjectService: SubjectService,
    protected subjectFormService: SubjectFormService,
    protected specService: SpecService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareSpec = (o1: ISpec | null, o2: ISpec | null): boolean => this.specService.compareSpec(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subject }) => {
      this.subject = subject;
      if (subject) {
        this.updateForm(subject);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const subject = this.subjectFormService.getSubject(this.editForm);
    if (subject.id !== null) {
      this.subscribeToSaveResponse(this.subjectService.update(subject));
    } else {
      this.subscribeToSaveResponse(this.subjectService.create(subject));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubject>>): void {
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

  protected updateForm(subject: ISubject): void {
    this.subject = subject;
    this.subjectFormService.resetForm(this.editForm, subject);

    this.specsSharedCollection = this.specService.addSpecToCollectionIfMissing<ISpec>(this.specsSharedCollection, subject.spec);
  }

  protected loadRelationshipsOptions(): void {
    this.specService
      .query()
      .pipe(map((res: HttpResponse<ISpec[]>) => res.body ?? []))
      .pipe(map((specs: ISpec[]) => this.specService.addSpecToCollectionIfMissing<ISpec>(specs, this.subject?.spec)))
      .subscribe((specs: ISpec[]) => (this.specsSharedCollection = specs));
  }
}
