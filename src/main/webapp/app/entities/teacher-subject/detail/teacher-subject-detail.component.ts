import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ITeacherSubject } from '../teacher-subject.model';

@Component({
  standalone: true,
  selector: 'jhi-teacher-subject-detail',
  templateUrl: './teacher-subject-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TeacherSubjectDetailComponent {
  @Input() teacherSubject: ITeacherSubject | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
