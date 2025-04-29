package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.DesignTemplate;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.repository.DesignTemplateRepository;
import com.techbytedev.signboardmanager.repository.UserDesignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Nên dùng Transactional
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Dùng để kiểm tra userId

@Service
public class DesignService {

    private static final Logger logger = LoggerFactory.getLogger(DesignService.class);

    private final UserDesignRepository userDesignRepository;
    private final DesignTemplateRepository designTemplateRepository;
    // private final CanvaService canvaService; // Tạm thời loại bỏ nếu service này không tạo/export design nữa

    // Cập nhật Constructor
    public DesignService(UserDesignRepository userDesignRepository, DesignTemplateRepository designTemplateRepository /*, CanvaService canvaService */) {
        this.userDesignRepository = userDesignRepository;
        this.designTemplateRepository = designTemplateRepository;
        // this.canvaService = canvaService;
    }

    public List<DesignTemplate> getTemplates(String category) {
        return designTemplateRepository.findByCategoryAndIsActiveTrueAndDeletedAtIsNull(category);
    }

    /**
     * Tạo bản ghi UserDesign ban đầu trong DB.
     * Không gọi Canva API ở đây.
     * @param user Người dùng tạo thiết kế
     * @param designName Tên thiết kế (có thể là tạm thời)
     * @param designType Loại thiết kế (ví dụ: 'Poster')
     * @return UserDesign đã được lưu với status DRAFT, canvaDesignId là null.
     */
    @Transactional // Đảm bảo tính nhất quán
    public UserDesign createDesign(User user, String designName, String designType) {
        logger.info("Creating initial DB record for design '{}' for user {}", designName, user.getId());
        UserDesign userDesign = new UserDesign();
        userDesign.setUser(user);
        userDesign.setDesignName(designName);
        // userDesign.setDesignType(designType); // Nếu có trường designType trong Entity
        userDesign.setCanvaDesignId(null); // Canva ID sẽ được cập nhật sau
        userDesign.setCanvaPreviewUrl(null); // Sẽ cập nhật URL xem trước/chỉnh sửa sau
        userDesign.setCanvaExportLink(null);
        userDesign.setStatus(UserDesign.Status.DRAFT); // Trạng thái ban đầu là bản nháp
        userDesign.setCreatedAt(LocalDateTime.now());
        userDesign.setUpdatedAt(LocalDateTime.now());
        // Không set submittedAt, deletedAt

        return userDesignRepository.save(userDesign);
    }

    /**
     * Cập nhật UserDesign với thông tin từ Canva sau khi frontend tạo thành công.
     * Đây là phương thức mà DesignController sẽ gọi.
     * @param designId ID của UserDesign trong DB cần cập nhật
     * @param userId ID của người dùng thực hiện (để kiểm tra quyền)
     * @param canvaDesignId ID thiết kế từ Canva
     * @param canvaEditUrl URL chỉnh sửa từ Canva (lưu vào trường canvaPreviewUrl hoặc canvaEditUrl của Entity)
     * @return UserDesign đã được cập nhật.
     * @throws IllegalArgumentException nếu designId không tồn tại.
     * @throws SecurityException nếu userId không khớp với chủ sở hữu thiết kế.
     */
    @Transactional
    public UserDesign saveCanvaDesignInfo(Integer designId, Integer userId, String canvaDesignId, String canvaEditUrl) {
        logger.info("Updating design record {} for user {} with Canva ID {}", designId, userId, canvaDesignId);
        UserDesign userDesign = userDesignRepository.findById(designId)
                .orElseThrow(() -> {
                    logger.warn("Design not found with ID: {}", designId);
                    return new IllegalArgumentException("Design not found with ID: " + designId);
                });

        // Kiểm tra quyền sở hữu
        if (!Objects.equals(userDesign.getUser().getId(), userId)) {
             logger.error("User {} attempted to save Canva info for design {} owned by user {}", userId, designId, userDesign.getUser().getId());
             throw new SecurityException("User does not have permission to update this design.");
        }

        userDesign.setCanvaDesignId(canvaDesignId);
        // Lưu canvaEditUrl vào trường canvaPreviewUrl hoặc canvaEditUrl (tùy thuộc vào Entity của bạn)
        // Dựa theo schema bạn cung cấp, có vẻ nên lưu vào canva_preview_url hoặc thêm trường mới canva_edit_url
        userDesign.setCanvaPreviewUrl(canvaEditUrl); // <<=== LƯU Ý TRƯỜNG NÀY TRONG ENTITY
        userDesign.setUpdatedAt(LocalDateTime.now());

        return userDesignRepository.save(userDesign);
    }

    /**
     * Phương thức saveDesign cũ - Xem xét có cần giữ lại không?
     * Phương thức này không kiểm tra user và có tham số khác.
     * Nếu không cần nữa thì có thể xóa đi.
     */
    // public UserDesign saveDesign(Integer designId, String canvaDesignId, String canvaPreviewUrl) {
    //     UserDesign userDesign = userDesignRepository.findById(designId)
    //             .orElseThrow(() -> new IllegalArgumentException("Design not found"));
    //     userDesign.setCanvaDesignId(canvaDesignId);
    //     userDesign.setCanvaPreviewUrl(canvaPreviewUrl);
    //     userDesign.setStatus(UserDesign.Status.DRAFT); // Có nên reset status ở đây?
    //     userDesign.setUpdatedAt(LocalDateTime.now());
    //     return userDesignRepository.save(userDesign);
    // }


    /**
     * Đánh dấu thiết kế là đã gửi.
     * @param designId ID của thiết kế cần gửi
     * @param userId ID của người dùng gửi (để kiểm tra quyền)
     * @return UserDesign đã được cập nhật trạng thái.
     * @throws IllegalArgumentException nếu designId không tồn tại.
     * @throws SecurityException nếu userId không khớp.
     * @throws IllegalStateException nếu thiết kế không ở trạng thái hợp lệ để gửi (vd: không phải DRAFT).
     */
    @Transactional
    public UserDesign submitDesign(Integer designId, Integer userId) /* throws Exception */ { // Bỏ throws Exception nếu không cần thiết
        logger.info("Submitting design {} for user {}", designId, userId);
        UserDesign userDesign = userDesignRepository.findById(designId)
                .orElseThrow(() -> {
                     logger.warn("Design not found for submission with ID: {}", designId);
                     return new IllegalArgumentException("Design not found with ID: " + designId);
                });

        // Kiểm tra quyền sở hữu
        if (!Objects.equals(userDesign.getUser().getId(), userId)) {
            logger.error("User {} attempted to submit design {} owned by user {}", userId, designId, userDesign.getUser().getId());
            throw new SecurityException("User does not have permission to submit this design.");
        }

        // Kiểm tra trạng thái hiện tại (chỉ cho phép gửi từ DRAFT chẳng hạn)
        if (userDesign.getStatus() != UserDesign.Status.DRAFT) {
             logger.warn("Attempted to submit design {} which is not in DRAFT status (current: {})", designId, userDesign.getStatus());
             throw new IllegalStateException("Design cannot be submitted from its current status: " + userDesign.getStatus());
        }

        // Kiểm tra xem đã có Canva Design ID chưa?
        if (userDesign.getCanvaDesignId() == null || userDesign.getCanvaDesignId().isEmpty()) {
             logger.warn("Attempted to submit design {} without a valid Canva Design ID.", designId);
             throw new IllegalStateException("Cannot submit design before it is linked to Canva.");
        }

        // **Tùy chọn: Gọi export ở đây hoặc không**
        // Nếu muốn export khi submit:
        // try {
        //     String exportUrl = canvaService.exportDesign(userDesign.getCanvaDesignId());
        //     userDesign.setCanvaExportLink(exportUrl);
        //     logger.info("Export link generated for design {}: {}", designId, exportUrl);
        // } catch (Exception e) {
        //     logger.error("Failed to export Canva design {} during submission", userDesign.getCanvaDesignId(), e);
        //     // Quyết định xem có nên dừng việc submit hay không nếu export lỗi
        //     // throw new RuntimeException("Failed to export design from Canva.", e);
        // }

        userDesign.setStatus(UserDesign.Status.SUBMITTED); // Chuyển trạng thái
        userDesign.setSubmittedAt(LocalDateTime.now()); // Ghi nhận thời gian gửi
        userDesign.setUpdatedAt(LocalDateTime.now());

        return userDesignRepository.save(userDesign);
    }

    public List<UserDesign> getUserDesigns(Integer userId) {
        return userDesignRepository.findByUserIdAndDeletedAtIsNull(userId);
    }

    public List<UserDesign> getSubmittedDesigns() {
        // Cân nhắc thêm phân trang (Paging) ở đây nếu danh sách lớn
        return userDesignRepository.findByStatusAndDeletedAtIsNull(UserDesign.Status.SUBMITTED);
    }

    // Bổ sung các phương thức khác nếu cần (ví dụ: lấy chi tiết 1 design, cập nhật trạng thái bởi admin,...)
}
