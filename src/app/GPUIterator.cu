extern "C"
// process definition for a thread of 1 pixel
__global__ void iterate(int size, float* zReal, float* zImag, float* cReal, float* cImag, int* n) {
    // block and thread IDs to workspace coordinates
    int ix = blockIdx.x * blockDim.x + threadIdx.x;
    int iy = blockIdx.y * blockDim.y + threadIdx.y;

    // processing all the data within the grid bounds
    if((ix < size) && (iy < size)) {
        int i = 0;
        float rReal = 0.0f;
        float rImag = 0.0f;
        float rMag = 0.0f;
        for(; i < 500; ++i) {
            rReal = (zReal[iy * size + ix] * zReal[iy * size + ix]) - (zImag[iy * size + ix] * zImag[iy * size + ix]) + cReal[iy * size + ix];
            rImag = 2.0f * zReal[iy * size + ix] * zImag[iy * size + ix] + cImag[iy * size + ix];
            rMag = rReal * rReal + rImag * rImag;
            if(rMag >= 4)
                break;
            zReal[iy * size + ix] = rReal;
            zImag[iy * size + ix] = rImag;
        }
        n[iy * size + ix] = i;
    }
}