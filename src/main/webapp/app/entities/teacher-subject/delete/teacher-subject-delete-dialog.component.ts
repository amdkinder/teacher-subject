import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITeacherSubject } from '../teacher-subject.model';
import { TeacherSubjectService } from '../service/teacher-subject.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './teacher-subject-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TeacherSubjectDeleteDialogComponent {
  teacherSubject?: ITeacherSubject;

  constructor(protected teacherSubjectService: TeacherSubjectService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teacherSubjectService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
