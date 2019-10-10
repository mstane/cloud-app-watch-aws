import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CloudappwatchTestModule } from '../../../test.module';
import { ServerDeleteDialogComponent } from 'app/entities/server/server-delete-dialog.component';
import { ServerService } from 'app/entities/server/server.service';

describe('Component Tests', () => {
  describe('Server Management Delete Component', () => {
    let comp: ServerDeleteDialogComponent;
    let fixture: ComponentFixture<ServerDeleteDialogComponent>;
    let service: ServerService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CloudappwatchTestModule],
        declarations: [ServerDeleteDialogComponent]
      })
        .overrideTemplate(ServerDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ServerDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ServerService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
