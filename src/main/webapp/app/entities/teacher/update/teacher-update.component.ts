import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TeacherFormService, TeacherFormGroup } from './teacher-form.service';
import { ITeacher } from '../teacher.model';
import { TeacherService } from '../service/teacher.service';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';
import { ISpec } from 'app/entities/spec/spec.model';
import { SpecService } from 'app/entities/spec/service/spec.service';
import { AcademicRank } from 'app/entities/enumerations/academic-rank.model';

@Component({
  standalone: true,
  selector: 'jhi-teacher-update',
  templateUrl: './teacher-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeacherUpdateComponent implements OnInit {
  isSaving = false;
  teacher: ITeacher | null = null;
  academicRankValues = Object.keys(AcademicRank);

  staffSharedCollection: IStaff[] = [];
  specsSharedCollection: ISpec[] = [];

  editForm: TeacherFormGroup = this.teacherFormService.createTeacherFormGroup();

  constructor(
    protected teacherService: TeacherService,
    protected teacherFormService: TeacherFormService,
    protected staffService: StaffService,
    protected specService: SpecService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareStaff = (o1: IStaff | null, o2: IStaff | null): boolean => this.staffService.compareStaff(o1, o2);

  compareSpec = (o1: ISpec | null, o2: ISpec | null): boolean => this.specService.compareSpec(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teacher }) => {
      this.teacher = teacher;
      if (teacher) {
        this.updateForm(teacher);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teacher = this.teacherFormService.getTeacher(this.editForm);
    if (teacher.id !== null) {
      this.subscribeToSaveResponse(this.teacherService.update(teacher));
    } else {
      this.subscribeToSaveResponse(this.teacherService.create(teacher));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeacher>>): void {
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

  protected updateForm(teacher: ITeacher): void {
    this.teacher = teacher;
    this.teacherFormService.resetForm(this.editForm, teacher);

    this.staffSharedCollection = this.staffService.addStaffToCollectionIfMissing<IStaff>(this.staffSharedCollection, teacher.staff);
    this.specsSharedCollection = this.specService.addSpecToCollectionIfMissing<ISpec>(this.specsSharedCollection, teacher.spec);
  }

  protected loadRelationshipsOptions(): void {
    this.staffService
      .query()
      .pipe(map((res: HttpResponse<IStaff[]>) => res.body ?? []))
      .pipe(map((staff: IStaff[]) => this.staffService.addStaffToCollectionIfMissing<IStaff>(staff, this.teacher?.staff)))
      .subscribe((staff: IStaff[]) => (this.staffSharedCollection = staff));

    this.specService
      .query()
      .pipe(map((res: HttpResponse<ISpec[]>) => res.body ?? []))
      .pipe(map((specs: ISpec[]) => this.specService.addSpecToCollectionIfMissing<ISpec>(specs, this.teacher?.spec)))
      .subscribe((specs: ISpec[]) => (this.specsSharedCollection = specs));
  }
}
