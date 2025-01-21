# Allergy Registration Screen

본 프로젝트의 알레르기 등록 페이지(UI 및 로직)으로, 사용자가 자신의 알레르기 정보를 손쉽게 관리할 수 있도록 구성.

## 주요 기능
- **카테고리별 토글**: 유제품, 견과류, 해산물, 고기류, 과일/야채를 펼치거나 접어 각 항목을 선택.
- **기타 항목 등록**: 사용자가 직접 추가 입력한 알레르기도 Chip 형태로 등록 가능.
- **ChipGroup 표시**: 선택된 알레르기 항목들이 Chip 형태로 시각화되어, 클릭 한 번으로 제거 가능.
- **토글 확장 시 스크롤**: 선택 항목이 많아져 화면을 초과해도 부드럽게 스크롤 가능.

## 화면 구조
1. **Header 영역**: 뒤로가기(ImageView) + 타이틀(TextView).
2. **ScrollView 영역**  
   - 토글(LinearLayout) + ListView: 카테고리별로 항목을 열고 닫을 수 있음.
   - 기타 입력 칸(LinearLayout + EditText + 등록 버튼).
   - 알레르기 ChipGroup(ChipGroup) : 선택된 항목들을 모아서 보여줌.
3. **하단 SAVE 버튼**: 전체 알레르기 정보를 서버(DB)나 로컬 DB에 저장할 수 있도록 연동.

## 핵심 코드 구조
- **activity_allergy.xml**: 전체 UI 배치 (ConstraintLayout + ScrollView + ChipGroup).
- **AllergyActivity.kt**  
  - `setupListView()`: 각 카테고리별 ListView 어댑터 및 토글 이벤트.
  - `addChip()`, `removeChip()`: ChipGroup에 알레르기 항목 추가/제거 로직.
  - `buttonRegister`, `buttonSave`: 새로운 항목 등록 및 저장 처리.

## 기술 스택
- **Android + Kotlin**
- **ConstraintLayout**: 상하단 고정, 중앙 부분 스크롤.
- **ListView** + **ChipGroup**: 다중 항목 선택 및 시각화.
- **ScrollView**: 내용이 많을 때 세로 스크롤 지원.
- **(추가 예정) Retrofit/Volley**: 백엔드 API 연동 시.

## 사용 방법
1. **카테고리 토글 클릭** → 하위 항목 펼치기/접기.
2. **체크된 항목**은 Chip 형태로 추가 → 필요 시 Chip의 X(닫기 아이콘)로 삭제.
3. **기타 항목 입력** 후 "등록" 버튼 → Chip 추가.
4. "SAVE" 버튼 → 현재 선택된 모든 알레르기 정보 저장(백엔드 API 연동 필요).

## 데모
![image](https://github.com/user-attachments/assets/4c8f0448-89a3-4082-8fb3-3163614627b9)
![image](https://github.com/user-attachments/assets/03d19f46-77ad-4a91-9356-bf3cf1152864)


## 향후 개선 사항
- 서버 연동 (Retrofit, Room DB 등) 
- UI 디자인 보완 (Material Design 적용)
- 사용자별 알레르기 정보 동기화
